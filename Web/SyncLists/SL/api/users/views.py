from django.http import HttpResponse
from django.shortcuts import render


# Create your views here.
def index(request):
    return HttpResponse('user index')


def users(request):
    if request.method == 'POST':
        create_user()


def user_id(request, user_id):
    if request.method == 'GET':
        get_user_by_id(user_id)
    elif request.method == 'PUT':
        edit_user(user_id)
    elif request.method == 'DELETE':
        delete_user(user_id)
    else:
        pass


def create_user():
    return HttpResponse('create user')


def login_user():
    return HttpResponse('login user')


def get_user_by_id(user_id):
    return HttpResponse('get user at id: ' + user_id)


def edit_user(user_id):
    return HttpResponse('edit user at id: ' + user_id)


def delete_user(user_id):
    return HttpResponse('delete user at id: ' + user_id)

