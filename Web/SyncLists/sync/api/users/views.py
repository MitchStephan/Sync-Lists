import sys

from django.core.exceptions import ObjectDoesNotExist
from django.db import IntegrityError
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from sync.api.utils import request_body_to_dict, validate_user_context, get_user_context
from sync.models import User


@csrf_exempt
def user(request):
    if request.method == 'GET':
        response = get_user(request)
    elif request.method == 'PUT':
        response = edit_user(request)
    elif request.method == 'DELETE':
        response = delete_user(request)
    elif request.method == 'POST':
        response = create_user(request)
    else:
        response = HttpResponse("Invalid request method of type {0}".format(request.method), status=400)
    return response


def create_user(request):
    status_code = 400
    request_body = {}
    if request.method == 'POST':
        try:
            request_body = request_body_to_dict(request)
            response = User.create(**request_body).single_to_json()
            status_code = 200
        except IntegrityError:
            response = 'A user with that email already exists.'
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
            u = User.login_user(**request_body)
            response = 'Invalid login (email exists)'
            if u:
                status_code = 200
                response = u.single_to_json()
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


def get_user(request):
    status_code = 400
    try:
        u_id = get_user_context(request)
        response = User.get_by_id(u_id).single_to_json()
        status_code = 200
    except ObjectDoesNotExist:
        response = 'User does not exist.'
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def edit_user(request):
    status_code = 400
    request_body = {}
    try:
        request_body = request_body_to_dict(request)
        u_id = get_user_context(request)
        u = User.get_by_id(u_id)
        response = u.edit(**request_body).single_to_json()
        status_code = 200
    except (TypeError, SyntaxError):
        response = 'Invalid request_body:\n{0}'.format(request_body)
    except:
        response = 'Unexpected exception!\nrequest_body:\n{0}\nexception:{1}'.format(request_body,
                                                                                     sys.exc_info())
    return HttpResponse(response, status=status_code)


def delete_user(request):
    status_code = 400
    try:
        u_id = get_user_context(request)
        u = User.get_by_id(u_id)
        u.delete()
        response = 'User id {0} has been deleted.'.format(u_id)
        status_code = 200
    except ObjectDoesNotExist:
        response = 'User does not exist.'
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def get_user_lists(request):
    status_code = 400
    if request.method == 'GET':
        try:
            u_id = get_user_context(request)
            u = User.get_by_id(u_id)
            response = User.to_json(u.get_lists())
        except ObjectDoesNotExist:
            response = 'User does not exist.'
        except:
            response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    else:
        response = "Invalid request method of type {0}".format(request.method)
    return HttpResponse(response, status=status_code)
