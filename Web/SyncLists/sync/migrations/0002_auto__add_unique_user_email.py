# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding unique constraint on 'User', fields ['email']
        db.create_unique(u'sync_user', ['email'])


    def backwards(self, orm):
        # Removing unique constraint on 'User', fields ['email']
        db.delete_unique(u'sync_user', ['email'])


    models = {
        u'sync.list': {
            'Meta': {'object_name': 'List'},
            'date_created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'list_owner': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'list owner'", 'to': u"orm['sync.User']"}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '225'}),
            'shared_users': ('django.db.models.fields.related.ManyToManyField', [], {'related_name': "'shared users'", 'symmetrical': 'False', 'to': u"orm['sync.User']"})
        },
        u'sync.task': {
            'Meta': {'object_name': 'Task'},
            'completed': ('django.db.models.fields.BooleanField', [], {}),
            'date_created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'list': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['sync.List']"}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '225'}),
            'task_owner': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['sync.User']"}),
            'visible': ('django.db.models.fields.BooleanField', [], {})
        },
        u'sync.user': {
            'Meta': {'object_name': 'User'},
            'date_created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'email': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '255'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '255'})
        }
    }

    complete_apps = ['sync']