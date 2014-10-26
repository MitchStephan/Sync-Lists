from ast import literal_eval


def request_body_to_dict(request):
    return literal_eval(request.read().decode('utf-8'))