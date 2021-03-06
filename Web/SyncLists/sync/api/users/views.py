from django.core.exceptions import ObjectDoesNotExist
from django.db import IntegrityError
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from sync.api.utils import request_body_to_dict, get_user_context, invalid_method, \
    unexpected_exception, invalid_request, does_not_exist, delete_response, invalid_user_context

from sync.models import User, List, Task


@csrf_exempt
def user(request):
    if request.method == 'GET':
        return get_user(request)
    elif request.method == 'PUT':
        return edit_user(request)
    elif request.method == 'DELETE':
        return delete_user(request)
    elif request.method == 'POST':
        return create_user(request)
    else:
        return invalid_method(request)


def setup_example_list(new_user):
    new_list = List.create("Example list", new_user)
    Task.create("Example task", new_list, new_user)


def create_user(request):
    status_code = 400
    request_body = {}
    try:
        request_body = request_body_to_dict(request)
        new_user = User.create(**request_body)
        response = new_user.single_to_json()
        setup_example_list(new_user)
        status_code = 200
    except IntegrityError:
        response = 'A user with that email already exists.'
    except (TypeError, SyntaxError):
        response = invalid_request(request, request_body)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


@csrf_exempt
def login_user(request):
    status_code = 400
    request_body = {}
    if request.method == 'POST':
        try:
            request_body = request_body_to_dict(request)
            u = User.login_user(**request_body)
            if u:
                status_code = 200
                response = u.single_to_json()
            else:
                response = 'Invalid login (email exists)'
        except ObjectDoesNotExist:
            response = does_not_exist('Email', request_body.get('email'))
        except (TypeError, SyntaxError):
            response = invalid_request(request, request_body)
        except:
            response = unexpected_exception(request, request_body)
    else:
        return invalid_method(request)
    return HttpResponse(response, status=status_code)


def get_user(request):
    status_code = 400
    request_body = {}
    u_id = get_user_context(request)
    if u_id:
        try:

            response = User.get_by_id(u_id).single_to_json()
            status_code = 200
        except ObjectDoesNotExist:
            response = does_not_exist('User', u_id)
        except:
            response = unexpected_exception(request, request_body)
    else:
        response = invalid_user_context(request)
    return HttpResponse(response, status=status_code)


def edit_user(request):
    status_code = 400
    request_body = {}
    u_id = get_user_context(request)
    if u_id:
        try:
            request_body = request_body_to_dict(request)
            u = User.get_by_id(u_id)
            response = u.edit(**request_body).single_to_json()
            status_code = 200
        except ObjectDoesNotExist:
            response = does_not_exist('User', u_id)
        except (TypeError, SyntaxError):
            response = invalid_request(request, request_body)
        except:
            response = unexpected_exception(request, request_body)
    else:
        response = invalid_user_context(request)
    return HttpResponse(response, status=status_code)


def delete_user(request):
    status_code = 400
    request_body = {}
    u_id = get_user_context(request)
    if u_id:
        try:
            u = User.get_by_id(u_id)
            u.delete()
            response = delete_response('User', u_id)
            status_code = 200
        except ObjectDoesNotExist:
            response = does_not_exist('User', u_id)
        except:
            response = unexpected_exception(request, request_body)
    else:
        response = invalid_user_context(request)
    return HttpResponse(response, status=status_code)


def get_user_lists(request):
    status_code = 400
    request_body = {}
    if request.method == 'GET':
        u_id = get_user_context(request)
        if u_id:
            try:
                u = User.get_by_id(u_id)
                response = List.to_json(u.get_lists())
                status_code = 200;
            except ObjectDoesNotExist:
                response = does_not_exist('User', u_id)
            except:
                response = unexpected_exception(request, request_body)
        else:
            response = invalid_user_context(request)
    else:
        return invalid_method(request)
    return HttpResponse(response, status=status_code)
