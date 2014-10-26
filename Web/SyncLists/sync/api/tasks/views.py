from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
import sys
from sync.api.utils import request_body_to_dict, get_user_context, validate_user_list_context
from sync.models import Task, List


@csrf_exempt
def task(request, l_id):
    if request.method == 'GET':
        response = get_list_tasks(request, l_id)
    elif request.method == 'POST':
        response = create_task(request, l_id)
    else:
        response = HttpResponse("Invalid request method of type {0}".format(request.method), status=400)
    return response

@csrf_exempt
def task_id(request, l_id, t_id):
    if request.method == 'GET':
        response = get_task(request, l_id, t_id)
    elif request.method == 'PUT':
        response = edit_task(request, l_id,  t_id)
    elif request.method == 'DELETE':
        response = delete_task(request, l_id, t_id)
    else:
        response = HttpResponse("Invalid request method of type {0}".format(request.method), status=400)
    return response


def get_list_tasks(request, l_id):
    status_code = 400
    try:
        list = List.get_by_id(l_id)
        if validate_user_list_context(request, list):
            response = Task.to_json(list.get_tasks())
            status_code = 200
        else:
            response = 'Invalid user context'
    except ObjectDoesNotExist:
        response = 'List id {0} does not exist.'.format(l_id)
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def create_task(request, l_id):
    status_code = 400
    request_body = {}
    if request.method == 'POST':
        try:
            request_body = request_body_to_dict(request)
            list = List.get_by_id(l_id)
            if validate_user_list_context(request, list):
                request_body['list'] = list
                request_body['task_owner'] = get_user_context(request)
                response = Task.create(**request_body).single_to_json()
                status_code = 200
            else:
                response = 'Invalid user context.'
        except (TypeError, SyntaxError, KeyError, ObjectDoesNotExist):
            response = 'Invalid request_body:\n{0}'.format(request_body)
        except:
            response = 'Unexpected exception!\nrequest_body:\n{0}\nexception:{1}'.format(request_body,
                                                                                         sys.exc_info())
    else:
        response = "Invalid request method of type {0}".format(request.method)
    return HttpResponse(response, status=status_code)


def get_task(request, l_id, t_id):
    status_code = 400
    try:
        list = List.get_by_id(l_id)
        if validate_user_list_context(request, list):
            response = Task.get_by_id(t_id).single_to_json()
            status_code = 200
        else:
            response = 'Invalid user context'
    except ObjectDoesNotExist:
        response = 'Task id {0} does not exist.'.format(t_id)
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def edit_task(request, l_id, t_id):
    status_code = 400
    request_body = {}
    try:
        list = List.get_by_id(l_id)
        if validate_user_list_context(request, list):
            t = Task.get_by_id(t_id)
            request_body = request_body_to_dict(request)
            response = t.edit(**request_body).single_to_json()
            status_code = 200
        else:
            response = 'Invalid user context'
    except ObjectDoesNotExist:
        response = 'Task id {0} does not exist.'.format(t_id)
    except (TypeError, SyntaxError):
        response = 'Invalid request_body:\n{0}'.format(request_body)
    except:
        response = 'Unexpected exception!\nrequest_body:\n{0}\nexception:{1}'.format(request_body,
                                                                                     sys.exc_info())
    return HttpResponse(response, status=status_code)


def delete_task(request, l_id, t_id):
    status_code = 400
    try:
        list = List.get_by_id(l_id)
        if validate_user_list_context(request, list):
            t = Task.get_by_id(t_id)
            t.delete()
            response = 'Task id {0} has been deleted.'.format(l_id)
            status_code = 200
        else:
            response = 'Invalid user context'
    except ObjectDoesNotExist:
        response = 'Task id {0} does not exist.'.format(t_id)
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)

