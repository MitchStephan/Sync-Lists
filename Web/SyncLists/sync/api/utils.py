from ast import literal_eval
from django.core.exceptions import ObjectDoesNotExist


def request_body_to_dict(request):
    return literal_eval(request.read().decode('utf-8'))


def get_user_context(request):
    return request.META.get('HTTP_USER_CONTEXT')


def validate_user_context(request, u_id):
    get_user_context(request) == u_id


def validate_user_list_context(request, list):
    try:
        return list.is_list_user(get_user_context(request))
    except ObjectDoesNotExist:
        return False
