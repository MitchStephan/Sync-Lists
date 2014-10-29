from django.core.exceptions import ObjectDoesNotExist

from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from sync.api.utils import request_body_to_dict, get_user_context, validate_user_context, invalid_method, \
    invalid_user_context, does_not_exist, unexpected_exception, invalid_request, delete_response

from sync.models import Task, List


@csrf_exempt
def task(request, l_id):
    if request.method == 'GET':
        return get_list_tasks(request, l_id)
    elif request.method == 'POST':
        return create_task(request, l_id)
    else:
        return invalid_method(request)


@csrf_exempt
def task_id(request, l_id, t_id):
    if request.method == 'GET':
        return get_task(request, l_id, t_id)
    elif request.method == 'PUT':
        return edit_task(request, l_id, t_id)
    elif request.method == 'DELETE':
        return delete_task(request, l_id, t_id)
    else:
        return invalid_method(request)


def get_list_tasks(request, l_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            response = Task.to_json(list.get_tasks())
            status_code = 200
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('List', l_id)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def create_task(request, l_id):
    status_code = 400
    request_body = {}
    try:
        request_body = request_body_to_dict(request)
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            request_body['list'] = list
            request_body['task_owner'] = get_user_context(request)
            response = Task.create(**request_body).single_to_json()
            status_code = 200
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('List', l_id)
    except (TypeError, SyntaxError, KeyError):
        response = invalid_request(request, request_body)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def get_task(request, l_id, t_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            response = Task.get_by_id(t_id).single_to_json()
            status_code = 200
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('Task', t_id)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def edit_task(request, l_id, t_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            t = Task.get_by_id(t_id)
            request_body = request_body_to_dict(request)
            response = t.edit(**request_body).single_to_json()
            status_code = 200
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('Task', t_id)
    except (TypeError, SyntaxError):
        response = invalid_request(request, request_body)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def delete_task(request, l_id, t_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        if validate_user_context(request, list):
            t = Task.get_by_id(t_id)
            t.delete()
            response = delete_response('Task', t_id)
            status_code = 200
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('Task', t_id)
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)

