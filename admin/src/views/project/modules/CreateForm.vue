<template>
  <a-modal
    :title="model && model.id ? '编辑项目' : '新建项目'"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @ok="() => { $emit('ok') }"
    @cancel="() => { $emit('cancel') }"
  >
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="formLayout">
        <a-form-item style="display: none;" label="id">
          <a-input v-decorator="['id', { initialValue: null }]" disabled />
        </a-form-item>
        <a-form-item label="项目名称">
          <a-input v-decorator="['project_name', {rules: [{required: true, max: 10, message: '项目名称不能为空且不能超过10个字符'}]}]" />
        </a-form-item>
        <a-form-item label="支持平台">
          <a-select
            mode="tags"
            style="width: 100%"
            placeholder="输入平台后回车（例如 iOS、安卓、Windows、Linux）"
            v-decorator="['platforms', {initialValue: []}]"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'

// 表单字段
const fields = ['project_name', 'platforms', 'id']

export default {
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    loading: {
      type: Boolean,
      default: () => false
    },
    model: {
      type: Object,
      default: () => null
    }
  },
  data () {
    this.formLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 }
      }
    }
    return {
      form: this.$form.createForm(this)
    }
  },
  created () {
    console.log('custom modal created')

    // 防止表单未注册
    fields.forEach(v => this.form.getFieldDecorator(v))

    // 当 model 发生改变时，为表单设置值
    this.$watch('model', () => {
      if (this.model) {
        const formData = pick(this.model, fields)
        if (typeof formData.platforms === 'string') {
          formData.platforms = formData.platforms ? formData.platforms.split(',') : []
        } else if (!formData.platforms) {
          formData.platforms = []
        }
        this.form.setFieldsValue(formData)
      }
    })
  }
}
</script>
