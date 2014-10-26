import sys

from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from sync.api.utils import request_body_to_dict
from sync.models import List


@csrf_exempt
def list_id(request, l_id):
    if request.method == 'GET':
        response = get_list_by_id(request, l_id)
    elif request.method == 'PUT':
        response = edit_list(request, l_id)
    elif request.method == 'DELETE':
        response = delete_list(request, l_id)
    else:
        response = HttpResponse("Invalid request method of type {0}".format(request.method), status=400)
    return response


@csrf_exempt
def list_id_tasks(request, l_id):
    if request.method == 'GET':
        response = get_list_tasks(request, l_id)
    else:
        response = HttpResponse("Invalid request method of type {0}".format(request.method), status=400)
    return response

@csrf_exempt
def create_list(request):
    status_code = 400
    request_body = {}
    if request.method == 'POST':
        try:
            request_body = request_body_to_dict(request)
            response = List.create(**request_body).single_to_json()
            status_code = 200
        except (TypeError, SyntaxError):
            response = 'Invalid request_body:\n{0}'.format(request_body)
        except:
            response = 'Unexpected exception!\nrequest_body:\n{0}\nexception:{1}'.format(request_body,
                                                                                         sys.exc_info())
    else:
        response = "Invalid request method of type {0}".format(request.method)
    return HttpResponse(response, status=status_code)


def get_list_by_id(request, l_id):
    status_code = 400
    try:
        response = List.get_by_id(l_id).single_to_json()
        status_code = 200
    except ObjectDoesNotExist:
        response = 'List id {0} does not exist.'.format(l_id)
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def edit_list(request, l_id):
    status_code = 400
    request_body = {}
    try:
        request_body = request_body_to_dict(request)
        list = List.get_by_id(l_id)
        response = list.edit(**request_body).single_to_json()
        status_code = 200
    except (TypeError, SyntaxError):
        response = 'Invalid request_body:\n{0}'.format(request_body)
    except:
        response = 'Unexpected exception!\nrequest_body:\n{0}\nexception:{1}'.format(request_body,
                                                                                     sys.exc_info())
    return HttpResponse(response, status=status_code)


def delete_list(request, l_id):
    status_code = 400
    try:
        list = List.get_by_id(l_id)
        list.delete()
        response = 'User id {0} has been deleted.'.format(l_id)
        status_code = 200
    except ObjectDoesNotExist:
        response = 'List id {0} does not exist.'.format(l_id)
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def get_list_users(request, l_id):
    status_code = 400
    try:
        response = List.to_json(List.get_by_id(l_id).get_all_users())
        status_code = 200
    except ObjectDoesNotExist:
        response = 'List id {0} does not exist.'.format(l_id)
    except:
        response = 'Unexpected exception!\n{0}'.format(sys.exc_info())
    return HttpResponse(response, status=status_code)


def get_list_tasks(request, l_id):
    return HttpResponse('get tasks for list ' + l_id)