from django.http import HttpResponse


def user_id(request, u_id):
    response = 0
    if request.method == 'GET':
        response = get_user_by_id(request, u_id)
    elif request.method == 'PUT':
        response = edit_user(request, u_id)
    elif request.method == 'DELETE':
        response = delete_user(request, u_id)
    else:
        pass
    return response


def create_user(request):
    return HttpResponse('create user')


def login_user(request):
    return HttpResponse('login user')


def get_user_by_id(request, u_id):
    return HttpResponse('get user at id: ' + u_id)


def edit_user(request, u_id):
    return HttpResponse('edit user at id: ' + u_id)


def delete_user(request, u_id):
    return HttpResponse('delete user at id: ' + u_id)


def get_user_lists(request, u_id):
    return HttpResponse('get lists for user ' + u_id)

