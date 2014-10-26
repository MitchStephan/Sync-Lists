from ast import literal_eval

from django.core import serializers
from django.db import models


class User(models.Model):
    # id (pk)
    email = models.CharField(max_length=255)
    # should we have a password in alpha?
    password = models.CharField(max_length=255)
    # list_lists = models.ManyToManyField('Lists')
    date_created = models.DateTimeField(auto_now_add=True)

    @staticmethod
    def get_by_id(pk):
        return User.objects.get(pk=pk)

    @staticmethod
    def get_by_email(email):
        return User.objects.get(email=email)

    @staticmethod
    def create(email, password):
        new_user = User.objects.create(email=email, password=password)
        new_user.save()
        return new_user

    def edit(self, email, password):
        self.email = email
        self.password = password
        self.save()
        return self

    @staticmethod
    def is_valid_login(email, password):
        user = User.get_by_email(email)
        if user.email == email and user.password == password:
            return True
        else:
            return False

    def __unicode__(self):
        return 'User; pk:{0}, email:{1}, date_created:{2}'.format(self.pk, self.email, self.date_created)

    def get_lists(self):
        return List.objects.filter(list_owner=self) | List.objects.filter(shared_users=self)

    # noinspection PyRedundantParentheses
    def single_to_json(self):
        return serializers.serialize("json", [self], fields=('email, date_created'))[1:-1]

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
    def create(name, list_owner, shared_user):
        new_list = List.objects.create(name=name, list_owner=list_owner, shared_user=shared_user)
        new_list.save()
        return new_list

    def edit(self, name, list_owner, shared_user):
        self.name = name
        self.list_owner = list_owner
        self.shared_users = shared_user
        self.save()

    def __unicode__(self):
        return 'List; pk:{0}, name:{1}, list_owner:{2}, date_created:{3} '.format(self.pk, self.name, self.list_owner,
                                                                                  self.date_created)

    # noinspection PyRedundantParentheses
    def single_to_json(self):
        return serializers.serialize("json", [self], fields=("name, list_owner, shared_users, date_created"))[1:-1]

    # noinspection PyRedundantParentheses
    @staticmethod
    def to_json(list):
        return serializers.serialize("json", list, fields=("name, list_owner, shared_users, date_created"))


class Task(models.Model):
    # id (pk)
    name = models.CharField(max_length=225)
    # order of task in list
    # order = models.ImageField()
    list = models.ForeignKey(List)
    completed = models.BooleanField()
    visible = models.BooleanField()
    # date_due = models.DateTimeField()
    task_owner = models.ForeignKey(User)
    date_created = models.DateTimeField(auto_now_add=True)

    @staticmethod
    def get_by_id(pk):
        return Task.objects.get(pk=pk)

    @staticmethod
    def create(name, list, completed, visible, task_owner):
        new_task = Task.objects.create(name=name, list=list, completed=completed, visible=visible,
                                       task_owner=task_owner)
        new_task.save()
        return new_task

    def edit(self, name, list, completed, visible, task_owner):
        self.name = name
        self.list = list
        self.completed = completed
        self.visible = visible
        self.task_owner = task_owner
        self.save()

    def __unicode__(self):
        return 'Task; pk:{0}, name:{1}, list:{2}, completed:{3}, visible:{4}, task_owner:{5}, date_created:{6}'.format(
            self.pk, self.name, self.list, self.completed, self.visible, self.task_owner, self.date_created)

    def single_to_json(self):
        return serializers.serialize("json", [self])[1:-1]

    @staticmethod
    def to_json(list):
        return serializers.serialize("json", list)