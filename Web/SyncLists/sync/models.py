import datetime
from django.core import serializers
from django.db import models
from sync.api.utils import json_to_dict, dict_to_json


class User(models.Model):
    # id (pk)
    email = models.CharField(max_length=255, unique=True)
    password = models.CharField(max_length=255)
    date_created = models.DateTimeField(auto_now_add=True)
    sharing_enabled = models.BooleanField(default=True)

    @staticmethod
    def get_by_id(pk):
        return User.objects.get(pk=pk)

    @staticmethod
    def get_by_email(email):
        return User.objects.get(email=email)

    @staticmethod
    def create(email, password, sharing_enabled=True):
        new_user = User.objects.create(email=email, password=password, sharing_enabled=sharing_enabled)
        new_user.save()
        return new_user

    def edit(self, email, sharing_enabled, password=None):
        self.email = email
        self.sharing_enabled = sharing_enabled
        if not password is None:
            self.password = password
        self.save()
        return self

    @staticmethod
    def login_user(email, password):
        user = User.get_by_email(email)
        if user.email == email and user.password == password:
            return user
        else:
            return None

    def __unicode__(self):
        return 'User; pk:{0}, email:{1}, date_created:{2}, sharing_enabled:{3}'.format(self.pk, self.email,
                                                                                       self.date_created,
                                                                                       self.sharing_enabled)

    def get_lists(self):
        return List.objects.filter(list_owner=self) | List.objects.filter(shared_users=self)

    # noinspection PyRedundantParentheses
    def single_to_json(self):
        return serializers.serialize("json", [self], fields=('email, date_created, sharing_enabled'))[1:-1]

    # noinspection PyRedundantParentheses
    @staticmethod
    def to_json(list):
        return serializers.serialize("json", list, fields=('email, date_created'))


class List(models.Model):
    # id (pk)
    # list of users shared
    name = models.CharField(max_length=225)
    list_owner = models.ForeignKey(User, related_name="list owner")
    shared_users = models.ManyToManyField('User', related_name="shared users")
    # date_due = models.DateTimeField()
    date_created = models.DateTimeField(auto_now_add=True)

    @staticmethod
    def get_by_id(pk):
        return List.objects.get(pk=pk)


    @staticmethod
    def get_by_email(email):
        return List.objects.get(email=email)


    @staticmethod
    def create(name, list_owner):
        if not isinstance(list_owner, User):
            list_owner = User.get_by_id(list_owner)
        new_list = List.objects.create(name=name, list_owner=list_owner)
        new_list.save()
        return new_list

    def edit(self, name):
        self.name = name
        self.save()
        return self

    def __unicode__(self):
        return 'List; pk:{0}, name:{1}, list_owner:{2}, date_created:{3} '.format(self.pk, self.name, self.list_owner,
                                                                                  self.date_created)

    def get_all_users(self):
        return [self.list_owner] + list(self.shared_users.all())

    def is_list_user(self, u_id):
        return User.get_by_id(u_id) in self.get_all_users()

    def get_tasks(self):
        return Task.objects.filter(list=self)

    def add_shared_user(self, user):
        if not isinstance(user, User):
            user = User.get_by_email(user)
        if user.sharing_enabled and not user == self.list_owner:
            self.shared_users.add(user)
            return True
        return False

    def delete_shared_user(self, user):
        if not isinstance(user, User):
            user = User.get_by_email(user)
        self.shared_users.remove(user)

    def list_delete(self, user):
        if not isinstance(user, User):
            user = User.get_by_id(user)
        # if shared user, just remove user from shared_users
        if user in self.shared_users.all():
            self.delete_shared_user(user)
            return True
        # if owner, clean up and delete list
        elif user == self.list_owner:
            for task in self.get_tasks():
                task.delete()
            self.delete()
            return True
        return False

    # noinspection PyRedundantParentheses
    def single_to_json(self):
        json = serializers.serialize("json", [self], fields=("name, list_owner, shared_users, date_created"))[1:-1]
        json_dict = json_to_dict(json)
        json_dict['fields']['list_owner'] = self.list_owner.email
        json_dict['fields']['shared_users'] = [user.email for user in self.shared_users.all()]
        return dict_to_json(json_dict)

    # noinspection PyRedundantParentheses
    @staticmethod
    def to_json(list):
        json = serializers.serialize("json", list, fields=("name, list_owner, shared_users, date_created"))
        json_dict = json_to_dict(json)
        for l in json_dict:
            li = List.get_by_id(l['pk'])
            l['fields']['list_owner'] = li.list_owner.email
            l['fields']['shared_users'] = [user.email for user in li.shared_users.all()]
        return dict_to_json(json_dict)


class Task(models.Model):
    # id (pk)
    name = models.CharField(max_length=225)
    list = models.ForeignKey(List)
    completed = models.BooleanField()
    visible = models.BooleanField()
    # date_due = models.DateTimeField()
    task_owner = models.ForeignKey(User, related_name="task_owner")
    date_created = models.DateTimeField(auto_now_add=True)
    date_updated = models.DateTimeField(auto_now=True, default=datetime.datetime(2014, 1, 1, 1, 11))
    last_editor = models.ForeignKey(User, related_name="last_editor", default=None, null=True)


    @staticmethod
    def get_by_id(pk):
        return Task.objects.get(pk=pk)

    @staticmethod
    def create(name, list, task_owner):
        if not isinstance(list, List):
            list = List.get_by_id(list)
        if not isinstance(task_owner, User):
            task_owner = User.get_by_id(task_owner)
        new_task = Task.objects.create(name=name, list=list, completed=False, visible=True,
                                       task_owner=task_owner, last_editor=task_owner)
        new_task.save()
        return new_task

    def edit(self, name, completed, last_editor, visible=True):
        if not isinstance(last_editor, User):
            last_editor = User.get_by_id(last_editor)
        self.name = name
        self.completed = completed
        self.visible = visible
        self.last_editor = last_editor
        self.save()
        return self

    def __unicode__(self):
        return 'Task; pk:{0}, name:{1}, list:{2}, completed:{3}, task_owner:{5}, date_created:{6}, editor:{7}'.format(
            self.pk, self.name, self.list, self.completed, self.visible, self.task_owner, self.date_created,
            self.last_editor)

    def single_to_json(self):
        json = serializers.serialize("json", [self], fields=(
            "name, list, completed, task_owner, date_created, date_updated, last_editor"))[1:-1]
        last_editor = self.last_editor.email
        json_dict = json_to_dict(json)
        json_dict['fields']['last_editor'] = last_editor
        return dict_to_json(json_dict)

    @staticmethod
    def to_json(list):
        json = serializers.serialize("json", list, fields=(
            "name, list, completed, task_owner, date_created, date_updated, last_editor"))
        json_dict = json_to_dict(json)
        for t in json_dict:
            last_editor_id = t['fields']['last_editor']
            t['fields']['last_editor'] = User.get_by_id(last_editor_id).email
        return dict_to_json(json_dict)