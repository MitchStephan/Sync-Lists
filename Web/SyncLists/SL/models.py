from django.db import models


class User(models.Model):
    # id (pk)
    email = models.CharField(max_length=255)
    # should we have a password in alpha?
    password = models.CharField(max_length=255)
    date_created = models.DateTimeField()


class Task(models.Model):
    # id (pk)
    name = models.CharField(max_length=225)
    # order of task in list
    # order = models.ImageField()
    list = models.ForeignKey(List)
    completed = models.BooleanField()
    visible = models.BooleanField()
    date_created = models.DateTimeField()


class List(models.Model):
    # id (pk)
    # list of users shared
    name = models.CharField(max_length=225)
    owner = models.ForeignKey(User)
    shared_users = models.ManyToManyField('User')
    date_due = models.DateTimeField()
    date_created = models.DateTimeField()