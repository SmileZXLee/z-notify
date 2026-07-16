<template>
  <a-modal
    :title="model && model.id ? '更新通知' : '新建通知'"
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
        <a-form-item label="通知标题">
          <a-input placeholder="请输入通知标题" :maxlength="10" show-count v-decorator="['title', {rules: [{required: true, max: 100, message: '通知标题不能为空且不能超过100个字符'}]}]" />
        </a-form-item>
        <a-form-item label="通知内容">
          <a-textarea :maxlength="200" show-count :autosize="{ minRows: 2, maxRows: 3 }" placeholder="请输入通知内容" v-decorator="['content', {rules: [{required: true, max: 200, message: '通知内容不能为空且不能超过200个字符'}]}]" />
        </a-form-item>
        <a-form-item label="过期时间">
          <!-- eslint-disable-next-line vue/max-attributes-per-line -->
          <a-date-picker style="width: 100%" show-time :disabled-date="disabledDate" placeholder="请选择过期时间(为空代表不限制)" valueFormat="YYYY-MM-DD HH:mm:ss" v-decorator="['expiretime', {rules: [{required: false}]}]"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'
// 表单字段
const fields = ['title', 'content', 'expiretime', 'id']

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
