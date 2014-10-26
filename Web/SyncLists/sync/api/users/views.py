from ast import literal_eval
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from sync.models import User
import sys


def user_id(request, u_id):
    if request.method == 'GET':
        response = get_user_by_id(request, u_id)
    elif request.method == 'PUT':
        response = edit_user(request, u_id)
    elif request.method == 'DELETE':
        response = delete_user(request, u_id)
    else:
        response = HttpResponse("Invalid request method of type {0}".format(request.method), status=400)
    return response


@csrf_exempt
def create_user(request):
    status_code = 400
    request_body = {}
    if request.method == 'POST':
        try:
            request_body = literal_eval(request.read().decode('utf-8'))
            new_user = User.create(**request_body)
            response = '{0}'.format(new_user)
            status_code = 200
        except (TypeError, SyntaxError):
            response = 'Invalid request_body:\n{0}'.format(request_body)
        except:
            response = 'Unexpected exception!\nrequest_body:\n{0}\nexception:{1}'.format(request_body,
                                                                                         sys.exc_info())
    else:
        response = "Invalid request mothod of type {0}".format(request.method)
    return HttpResponse(response, status=status_code)


def login_user(request):
    return HttpResponse('login user')


def get_user_by_id(request, u_id):
    status_code = 400
    try:
        response = '{0}'.format(User.get_by_id(u_id))
        status_code = 200
    except:
        response = 'Unexpected exception!\n:{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def edit_user(request, u_id):
    return HttpResponse('edit user at id: ' + u_id)


def delete_user(request, u_id):
    return HttpResponse('delete user at id: ' + u_id)


def get_user_lists(request, u_id):
    return HttpResponse('get lists for user ' + u_id)

