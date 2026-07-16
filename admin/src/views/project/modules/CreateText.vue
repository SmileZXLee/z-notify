<template>
  <a-modal
    :title="model && model.id ? '更新文本' : '新建文本'"
    :width="600"
    :visible="visible"
    :confirmLoading="loading"
    @ok="() => { $emit('ok') }"
    @cancel="() => { $emit('cancel') }"
  >
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="formLayout">
        <!-- 检查是否有 id，有id是修改。其他是新增，新增不显示主键ID -->
        <a-form-item style="display: none;" label="id">
          <a-input v-decorator="['id', { initialValue: null }]" disabled />
        </a-form-item>
        <a-form-item label="文本key">
          <a-input placeholder="请输入文本key" :maxlength="10" show-count v-decorator="['key', {rules: [{required: true, max: 10, message: '文本key不能为空且不能超过10个字符'}]}]" />
        </a-form-item>
        <a-form-item label="文本value">
          <a-textarea :maxlength="200" show-count :autosize="{ minRows: 2, maxRows: 3 }" placeholder="请输入文本value" v-decorator="['value', {rules: [{required: true, max: 200, message: '文本value不能为空且不能超过200个字符'}]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'
// 表单字段
const fields = ['key', 'value', 'id']

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
      form: this.$form.createForm(this),
      disabledDate: current => {
        return false
      }
    }
  },
  created () {
    console.log('custom modal created')

    // 防止表单未注册
    fields.forEach(v => this.form.getFieldDecorator(v))

    // 当 model 发生改变时，为表单设置值
    this.$watch('model', () => {
      this.model && this.form.setFieldsValue(pick(this.model, fields))
    })
  }
}
</script>
