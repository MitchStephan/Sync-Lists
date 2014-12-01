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
def list_share(request, l_id):
    request_body = request_body_to_dict(request)
    if request.method == 'POST':
        action = request_body.get('action')
        if action == 'add':
            return add_shared_user(request, l_id, request_body)
        elif action == 'delete':
            return remove_shared_user(request, l_id, request_body)
        else:
            return HttpResponse("Invalid action of {0}".format(action), status=400)
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
            result = list.list_delete(get_user_context(request))
            if result:
                response = delete_response('List', l_id)
                status_code = 200
            else:
                response = "Something went wrong during deletion"
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


def add_shared_user(request, l_id, request_body):
    status_code = 400
    try:
        list = List.get_by_id(l_id)
        user_context = int(get_user_context(request))
        # only list owners can add people to lists
        if user_context == list.list_owner.pk:
            user_to_add = User.get_by_email(request_body.get('email'))
            if list.add_shared_user(user_to_add):
                response = "User {0} was added as a shared user to the list.".format(user_to_add.email)
                status_code = 200
            else:
                response = "Cannot share with user {0} because they disabled sharing or are the list owner.".format(
                    user_to_add.email)
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('Object', '')
    # except:
    #     response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)


def remove_shared_user(request, l_id, request_body):
    status_code = 400
    try:
        list = List.get_by_id(l_id)
        # checks if user is list owner or shared user
        if validate_user_context(request, list):
            user_to_remove = User.get_by_email(request_body.get('email'))
            if user_to_remove == list.list_owner:
                response = "Cannot remove list owner from list."
            else:
                user_context = get_user_context(request)
                if user_context == list.list_owner.pk:
                    # list owner may delete anyone except themselves which
                    # we already checked for
                    list.delete_shared_user(user_to_remove)
                    response = "User {0} has been removed as a shared user on the list".format(user_to_remove.email)
                    status_code = 200
                else:
                    # we are just a user on the list and we can only remove ourselves.
                    if get_user_context(request) == user_to_remove.pk:
                        list.delete_shared_user(user_to_remove)
                        response = "User {0} has been removed as a shared user on the list".format(user_to_remove.email)
                        status_code = 200
                    else:
                        response = "Cannot delete User {0} as you are not the list owner.".format(user_to_remove.email)
        else:
            response = invalid_user_context(request)
    except ObjectDoesNotExist:
        response = does_not_exist('Object', '')
    except:
        response = unexpected_exception(request, request_body)
    return HttpResponse(response, status=status_code)