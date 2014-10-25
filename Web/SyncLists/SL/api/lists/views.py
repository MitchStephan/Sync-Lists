from django.http import HttpResponse


# Create your views here.
def index(request):
    return HttpResponse('list index')


def list_id(request, l_id):
    response = 0
    if request.method == 'GET':
        response = get_list_by_id(request, l_id)
    elif request.method == 'PUT':
        response = edit_list(request, l_id)
    elif request.method == 'DELETE':
        response = delete_list(request, l_id)
    return response


def list_id_tasks(request, l_id):
    response = 0
    if request.method == 'GET':
        response = get_list_tasks(request, l_id)
    elif request.method == 'POST':
        response = create_task(request, l_id)
    return response


def create_list(request):
    return HttpResponse('create list')


def get_list_by_id(request, l_id):
    return HttpResponse('get list by id ' + l_id)


def edit_list(request, l_id):
    return HttpResponse('edit list by id ' + l_id)


def delete_list(request, l_id):
    return HttpResponse('delete list by id ' + l_id)


def get_list_users(request, l_id):
    return HttpResponse('get users who can use list ' + l_id)


def get_list_tasks(request, l_id):
    return HttpResponse('get tasks for list ' + l_id)


def create_task(request, l_id):
    return HttpResponse('create task for list ' + l_id)