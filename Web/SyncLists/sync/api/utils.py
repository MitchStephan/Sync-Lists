from ast import literal_eval

from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
import sys


def request_body_to_dict(request):
    return literal_eval(request.read().decode('utf-8'))


def get_user_context(request):
    return request.META.get('HTTP_USER_CONTEXT')


def validate_user_context(request, list):
    try:
        return list.is_list_user(get_user_context(request))
    except ObjectDoesNotExist:
        return False


def invalid_method(request):
    return HttpResponse("Invalid request method of type {0}.".format(request.method), status=400)


def unexpected_exception(request, request_body):
    return 'Unexpected exception!\n\nheaders:\n{0}\n\nrequest body:\n{1}\n\nexception:\n{2}'.format(request.META,
                                                                                                    request_body,
                                                                                                    sys.exc_info())


def invalid_request(request, request_body):
    return 'Invalid request!\n\nheaders:\n{0}\n\nrequest body:\n{1}'.format(request.META, request_body)


def does_not_exist(obj_str, obj_id):
    return '{0} {1} does not exist.'.format(obj_str, obj_id)


def invalid_user_context(request):
    return 'User context {0} is invalid.'.format(get_user_context(request))


def delete_response(obj_str, obj_id):
    return '{0} {1} has been deleted.'.format(obj_str, obj_id)