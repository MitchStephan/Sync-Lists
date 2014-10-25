from django.http import HttpResponse


# Create your views here.
def index(request):
    return HttpResponse('task index')


def task_id(request, t_id):
    response = 0
    if request.method == 'GET':
        response = get_task_by_id(request, t_id)
    elif request.method == 'PUT':
        response = edit_task(request, t_id)
    elif request.method == 'DELETE':
        response = delete_task(request, t_id)
    else:
        pass
    return response


def get_task_by_id(request, t_id):
    return HttpResponse('task by id '+ t_id)


def edit_task(request, t_id):
    return HttpResponse('edit task ' + t_id)


def delete_task(request, t_id):
    return HttpResponse('delete task ' + t_id)

