<template>
  <a-modal
    title="反馈详情"
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
        <a-form-item label="用户名">
          <a-input placeholder="无用户名" readonly v-decorator="['username']" />
        </a-form-item>
        <a-form-item label="联系方式">
          <a-input placeholder="无联系方式" readonly v-decorator="['contact']" />
        </a-form-item>
        <a-form-item label="反馈内容">
          <a-textarea :maxlength="500" readonly :autoSize="{ minRows: 4, maxRows: 6 }" placeholder="无反馈内容" v-decorator="['content']" />
        </a-form-item>
        <a-form-item label="附加内容">
          <a-textarea :maxlength="500" readonly :autoSize="{ minRows: 2, maxRows: 3 }" placeholder="无附加内容" v-decorator="['extra_info']" />
        </a-form-item>
        <a-form-item label="反馈图片" v-if="model && model.img_urls && model.img_urls.length">
          <div class="img-viewer-container" :images="model.img_urls" v-viewer>
            <img class="img-viewer-img" v-for="item in model.img_urls" :key="item" :src="item">
          </div>
        </a-form-item>
        <a-form-item label="回复内容">
          <a-textarea :maxlength="500" show-count :autoSize="{ minRows: 4, maxRows: 6 }" placeholder="请输入回复内容" v-decorator="['reply', {rules: [{max: 500, message: '回复内容不能为空且不能超过500个字符'}]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'
// 表单字段
const fields = ['username', 'contact', 'content', 'extra_info', 'reply', 'id']

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
      this.model && this.form.setFieldsValue(pick(this.model, fields))
    })
  }
}
</script>
<style lang="less" scoped>
  .img-viewer-container{
    display: flex;
    flex-wrap: wrap;
    .img-viewer-img{
      width: 70px;
      height: 70px;
      margin-right: 6px;
      margin-bottom: 6px;
      object-fit: cover;
      border-radius: 3px;
    }
  }
</style>
