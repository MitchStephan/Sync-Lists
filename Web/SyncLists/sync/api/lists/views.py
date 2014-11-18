from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from sync.api.utils import request_body_to_dict, validate_user_context, get_user_context, invalid_method, \
    invalid_request, unexpected_exception, invalid_user_context, does_not_exist, delete_response
from sync.models import List, User


@csrf_exempt
def list_id(request, l_id):
    if request.method == 'GET':
        return get_list(request, l_id)
    elif request.method == 'PUT':
        return edit_list(request, l_id)
    elif request.method == 'DELETE':
        return delete_list(request, l_id)
    else:
        return invalid_method(request)


@csrf_exempt
def list_users_id(request, l_id, u_id):
    if request.method == 'POST':
        return add_shared_user(request, l_id, u_id)
    elif request.method == 'DELETE':
        return remove_shared_user(request, l_id, u_id)
    else:
        return invalid_method(request)


@csrf_exempt
def create_list(request):
    status_code = 400
    request_body = {}
    if request.method == 'POST':
        try:
            request_body = request_body_to_dict(request)
            request_body['list_owner'] = get_user_context(request)
            response = List.create(**request_body).single_to_json()
            status_code = 200
        except ObjectDoesNotExist:
            response = does_not_exist('User', get_user_context(request))
        except (TypeError, SyntaxError, KeyError):
            response = invalid_request(request, request_body)
        except:
            response = unexpected_exception(request, request_body)
    else:
        return invalid_method(request)
    return HttpResponse(response, status=status_code)


def get_list(request, l_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            response = list.single_to_json()
            status_code = 200
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('List', l_id)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def edit_list(request, l_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            request_body = request_body_to_dict(request)
            response = list.edit(**request_body).single_to_json()
            status_code = 200
        else:
            response = invalid_user_context(request)
    except (TypeError, SyntaxError):
        response = invalid_request(request, request_body)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def delete_list(request, l_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            list.list_delete(get_user_context(request))
            response = delete_response('List', l_id)
            status_code = 200
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('List', l_id)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def get_list_users(request, l_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            response = User.to_json(list.get_all_users())
            status_code = 200
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('List', l_id)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def add_shared_user(request, l_id, u_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        user_context = int(get_user_context(request))
        if user_context == list.list_owner.pk:
            user_to_add = u_id
            if list.add_shared_user(user_to_add):
                response = "User {0} was added as a shared user to the list.".format(user_to_add)
                status_code = 200
            else:
                response = "Cannot share with user {0} because they disabled sharing or are list owner.".format(
                    user_to_add)
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('Object', '')
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def remove_shared_user(request, l_id, u_id):
    status_code = 400
    request_body = request.read().decode('utf-8')
    try:
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            user_to_remove = u_id
            if user_to_remove == list.list_owner.pk:
                response = "Cannot remove list owner from list."
            else:
                list.delete_shared_user(user_to_remove)
                response = "User {0} has been removed as a shared user on the list".format(user_to_remove)
                status_code = 200
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('Object', '')
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)