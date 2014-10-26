import sys

from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from sync.api.utils import request_body_to_dict
from sync.models import User


@csrf_exempt
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
            request_body = request_body_to_dict(request)
            response = User.create(**request_body).single_to_json()
            status_code = 200
        except (TypeError, SyntaxError):
            response = 'Invalid request_body:\n{0}'.format(request_body)
        except:
            response = 'Unexpected exception!\nrequest_body:\n{0}\nexception:{1}'.format(request_body,
                                                                                         sys.exc_info())
    else:
        response = "Invalid request method of type {0}".format(request.method)
    return HttpResponse(response, status=status_code)


@csrf_exempt
def login_user(request):
    status_code = 400
    request_body = {}
    if request.method == 'POST':
        try:
            request_body = request_body_to_dict(request)
            if User.is_valid_login(**request_body):
                status_code = 200
                response = 'Valid login'
            else:
                response = 'Invalid login'
        except ObjectDoesNotExist:
            response = 'Account with email {0} does not exist.'.format(request_body['email'])
        except (TypeError, SyntaxError):
            response = 'Invalid request_body:\n{0}'.format(request_body)
        except:
            response = 'Unexpected exception!\nrequest_body:\n{0}\nexception:{1}'.format(request_body,
                                                                                         sys.exc_info())
    else:
        response = "Invalid request method of type {0}".format(request.method)
    return HttpResponse(response, status=status_code)


def get_user_by_id(request, u_id):
    status_code = 400
    try:
        response = User.get_by_id(u_id).single_to_json()
        status_code = 200
    except ObjectDoesNotExist:
        response = 'User id {0} does not exist.'.format(u_id)
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def edit_user(request, u_id):
    status_code = 400
    request_body = {}
    try:
        request_body = request_body_to_dict(request)
        user = User.get_by_id(u_id)
        response = user.edit(**request_body).single_to_json()
        status_code = 200
    except (TypeError, SyntaxError):
        response = 'Invalid request_body:\n{0}'.format(request_body)
    except:
        response = 'Unexpected exception!\nrequest_body:\n{0}\nexception:{1}'.format(request_body,
                                                                                     sys.exc_info())
    return HttpResponse(response, status=status_code)


def delete_user(request, u_id):
    status_code = 400
    try:
        user = User.get_by_id(u_id)
        user.delete()
        response = 'User id {0} has been deleted.'.format(u_id)
        status_code = 200
    except ObjectDoesNotExist:
        response = 'User id {0} does not exist.'.format(u_id)
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def get_user_lists(request, u_id):
    status_code = 400
    if request.method == 'GET':
        try:
            user = User.get_by_id(u_id)
            response = User.to_json(user.get_lists())
        except ObjectDoesNotExist:
            response = 'User id {0} does not exist.'.format(u_id)
        except:
            response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    else:
        response = "Invalid request method of type {0}".format(request.method)
    return HttpResponse(response, status=status_code)
