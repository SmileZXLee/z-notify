<template>
  <a-modal
    :title="model && model.id ? '更新版本' : '新建版本'"
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
        <a-form-item label="版本号" :labelCol="{ span: 5 }" :wrapperCol="{ span: 17 }" style="margin-bottom: 8px;">
          <a-input placeholder="请输入版本号" :maxlength="10" show-count v-decorator="['version', {rules: [{required: true, max: 10, message: '版本号不能为空且不能超过10个字符'}]}]" />
        </a-form-item>

        <a-tabs default-active-key="global" size="small" style="margin-left: 10px; margin-right: 10px; margin-top: -5px;">
          <a-tab-pane key="global" tab="全局默认">
            <div style="padding-top: 5px;">
              <a-form-item label="更新内容" :labelCol="{ span: 5 }" :wrapperCol="{ span: 17 }" style="margin-bottom: 12px;">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
                  <a-radio-group v-model="activeLang" size="small" button-style="solid">
                    <a-radio-button value="zh-Hans">简体中文</a-radio-button>
                    <a-radio-button value="zh-Hant">繁体中文</a-radio-button>
                    <a-radio-button value="en">English</a-radio-button>
                  </a-radio-group>
                  <span v-if="activeLang === 'zh-Hans'" style="color: #ff4d4f; font-size: 12px; font-weight: 500;">* 必填</span>
                  <span v-else style="color: #8c8c8c; font-size: 12px;">选填</span>
                </div>
                <a-textarea
                  v-show="activeLang === 'zh-Hans'"
                  :maxlength="500"
                  show-count
                  :autosize="{ minRows: 4, maxRows: 6 }"
                  placeholder="请输入中文简体更新内容"
                  v-decorator="['content_zh_Hans', {rules: [{required: true, max: 500, message: '中文简体更新内容不能为空'}]}]"
                />
                <a-textarea
                  v-show="activeLang === 'zh-Hant'"
                  :maxlength="500"
                  show-count
                  :autosize="{ minRows: 4, maxRows: 6 }"
                  placeholder="请输入中文繁体更新内容（选填）"
                  v-decorator="['content_zh_Hant']"
                />
                <a-textarea
                  v-show="activeLang === 'en'"
                  :maxlength="500"
                  show-count
                  :autosize="{ minRows: 4, maxRows: 6 }"
                  placeholder="请输入英文更新内容（选填）"
                  v-decorator="['content_en']"
                />
              </a-form-item>
              <a-form-item label="配置内容" :labelCol="{ span: 5 }" :wrapperCol="{ span: 17 }" style="margin-bottom: 10px;">
                <a-textarea :maxlength="2000" show-count :autosize="{ minRows: 4, maxRows: 6 }" placeholder="请输入默认配置内容，可选" v-decorator="['config']" />
              </a-form-item>
              <a-form-item label="下载地址" :labelCol="{ span: 5 }" :wrapperCol="{ span: 17 }" style="margin-bottom: 10px;">
                <a-input placeholder="请输入默认下载地址，通配符 {version} 将被自动替换" show-count v-decorator="['download_url']" />
              </a-form-item>
            </div>
          </a-tab-pane>

          <a-tab-pane v-for="platform in platforms" :key="platform" :tab="platform" forceRender>
            <div style="padding-top: 5px;">
              <a-form-item label="发布状态" :labelCol="{ span: 5 }" :wrapperCol="{ span: 17 }" style="margin-bottom: 10px;">
                <a-switch
                  v-decorator="['enable_' + platform, { valuePropName: 'checked', initialValue: true }]"
                  @change="(val) => onEnableChange(platform, val)"
                />
                <span style="margin-left: 10px; color: #999; font-size: 13px;">在当前平台启用该版本</span>
              </a-form-item>

              <div v-show="activeEnables[platform] !== false">
                <a-form-item label="自定义覆盖" :labelCol="{ span: 5 }" :wrapperCol="{ span: 17 }" style="margin-bottom: 10px;">
                  <a-switch
                    v-decorator="['override_' + platform, { valuePropName: 'checked', initialValue: false }]"
                    @change="(val) => onOverrideChange(platform, val)"
                  />
                  <span style="margin-left: 10px; color: #999; font-size: 13px;">开启后，该平台可单独配置参数</span>
                </a-form-item>

                <div v-show="activeOverrides[platform]">
                  <a-form-item label="专属更新内容" :labelCol="{ span: 5 }" :wrapperCol="{ span: 17 }" style="margin-bottom: 12px;">
                    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
                      <a-radio-group
                        :value="activePlatformLangs[platform] || 'zh-Hans'"
                        @change="(e) => $set(activePlatformLangs, platform, e.target.value)"
                        size="small"
                        button-style="solid"
                      >
                        <a-radio-button value="zh-Hans">简体中文</a-radio-button>
                        <a-radio-button value="zh-Hant">繁体中文</a-radio-button>
                        <a-radio-button value="en">English</a-radio-button>
                      </a-radio-group>
                      <span style="color: #8c8c8c; font-size: 12px;">选填</span>
                    </div>
                    <a-textarea
                      v-show="(activePlatformLangs[platform] || 'zh-Hans') === 'zh-Hans'"
                      :maxlength="500"
                      show-count
                      :autosize="{ minRows: 4, maxRows: 6 }"
                      placeholder="不填则继承默认简体更新内容"
                      v-decorator="['content_zh_Hans_' + platform]"
                    />
                    <a-textarea
                      v-show="(activePlatformLangs[platform] || 'zh-Hans') === 'zh-Hant'"
                      :maxlength="500"
                      show-count
                      :autosize="{ minRows: 4, maxRows: 6 }"
                      placeholder="不填则继承默认繁体更新内容"
                      v-decorator="['content_zh_Hant_' + platform]"
                    />
                    <a-textarea
                      v-show="(activePlatformLangs[platform] || 'zh-Hans') === 'en'"
                      :maxlength="500"
                      show-count
                      :autosize="{ minRows: 4, maxRows: 6 }"
                      placeholder="不填则继承默认英文更新内容"
                      v-decorator="['content_en_' + platform]"
                    />
                  </a-form-item>
                  <a-form-item label="专属配置内容" :labelCol="{ span: 5 }" :wrapperCol="{ span: 17 }" style="margin-bottom: 10px;">
                    <a-textarea :maxlength="2000" show-count :autosize="{ minRows: 4, maxRows: 6 }" :placeholder="'不填则继承默认配置内容'" v-decorator="['config_' + platform]" />
                  </a-form-item>
                  <a-form-item label="专属下载地址" :labelCol="{ span: 5 }" :wrapperCol="{ span: 17 }" style="margin-bottom: 10px;">
                    <a-input :placeholder="'不填则继承默认下载地址，通配符 {version} 将被自动替换'" show-count v-decorator="['download_url_' + platform]" />
                  </a-form-item>
                </div>

                <div v-show="!activeOverrides[platform]" style="text-align: center; padding: 15px 0; color: #ccc;">
                  <a-icon type="info-circle" style="font-size: 20px; margin-bottom: 5px;" /><br/>
                  <span style="font-size: 13px;">当前平台将继承并使用全局默认配置</span>
                </div>
              </div>

              <div v-show="activeEnables[platform] === false" style="text-align: center; padding: 30px 0; color: #ff4d4f;">
                <a-icon type="close-circle" style="font-size: 20px; margin-bottom: 5px;" /><br/>
                <span style="font-size: 13px; font-weight: 500;">当前平台已禁用此版本</span><br/>
                <span style="font-size: 12px; color: #999;">客户端在此平台检测更新时将忽略此版本，仍使用历史版本。</span>
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'
// 基础字段列表
const basicFields = ['version', 'config', 'download_url', 'id']

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
    },
    platforms: {
      type: Array,
      default: () => []
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
      activeEnables: {},
      activeOverrides: {},
      activeLang: 'zh-Hans',
      activePlatformLangs: {},
      disabledDate: current => {
        return false
      }
    }
  },
  created () {
    console.log('custom modal created')

    // 防止基础表单字段未注册
    basicFields.forEach(v => this.form.getFieldDecorator(v))
    this.form.getFieldDecorator('content_zh_Hans', { rules: [{ required: true, message: '中文简体更新内容不能为空' }] })
    this.form.getFieldDecorator('content_zh_Hant')
    this.form.getFieldDecorator('content_en')

    // 当 model 发生改变时，为表单设置值
    this.$watch('model', () => {
      if (this.model) {
        const normalizedModel = { ...this.model }
        if (normalizedModel.downloadUrl !== undefined) {
          normalizedModel.download_url = normalizedModel.downloadUrl
        }
        if (normalizedModel.platformSettings !== undefined) {
          normalizedModel.platform_settings = normalizedModel.platformSettings
        }

        // 解析多语言更新日志
        let contentMap = {}
        if (normalizedModel.content) {
          try {
            contentMap = JSON.parse(normalizedModel.content)
          } catch (e) {
            contentMap = { 'zh-Hans': normalizedModel.content }
          }
        }

        // 回显基础字段
        const basicData = pick(normalizedModel, basicFields)
        basicData.download_url = typeof normalizedModel.download_url === 'string' ? normalizedModel.download_url : ''
        basicData.config = normalizedModel.config || ''
        basicData.content_zh_Hans = contentMap['zh-Hans'] || ''
        basicData.content_zh_Hant = contentMap['zh-Hant'] || ''
        basicData.content_en = contentMap['en'] || ''
        this.form.setFieldsValue(basicData)

        // 回显平台覆盖及发布状态字段
        const platformSettings = normalizedModel.platform_settings || {}
        const dynamicValues = {}
        const newOverrides = {}
        const newEnables = {}
        this.platforms.forEach(platform => {
          const override = platformSettings[platform] || {}
          const isEnabled = override.enable !== undefined ? !!override.enable : true
          const isOverridden = !!(override.download_url || override.downloadUrl || override.content || override.config)

          newEnables[platform] = isEnabled
          newOverrides[platform] = isOverridden

          // 解析平台多语言更新日志
          let platContentMap = {}
          if (override.content) {
            try {
              platContentMap = JSON.parse(override.content)
            } catch (e) {
              platContentMap = { 'zh-Hans': override.content }
            }
          }

          // 提前在表单注册动态字段，避免 lazy-rendering 导致 setFieldsValue 失败
          this.form.getFieldDecorator('enable_' + platform, { valuePropName: 'checked', initialValue: isEnabled })
          this.form.getFieldDecorator('override_' + platform, { valuePropName: 'checked', initialValue: isOverridden })
          this.form.getFieldDecorator('download_url_' + platform, { initialValue: override.download_url || override.downloadUrl || '' })
          this.form.getFieldDecorator('content_zh_Hans_' + platform, { initialValue: platContentMap['zh-Hans'] || '' })
          this.form.getFieldDecorator('content_zh_Hant_' + platform, { initialValue: platContentMap['zh-Hant'] || '' })
          this.form.getFieldDecorator('content_en_' + platform, { initialValue: platContentMap['en'] || '' })
          this.form.getFieldDecorator('config_' + platform, { initialValue: override.config || '' })

          dynamicValues['enable_' + platform] = isEnabled
          dynamicValues['override_' + platform] = isOverridden
          dynamicValues['download_url_' + platform] = override.download_url || override.downloadUrl || ''
          dynamicValues['content_zh_Hans_' + platform] = platContentMap['zh-Hans'] || ''
          dynamicValues['content_zh_Hant_' + platform] = platContentMap['zh-Hant'] || ''
          dynamicValues['content_en_' + platform] = platContentMap['en'] || ''
          dynamicValues['config_' + platform] = override.config || ''
        })
        this.activeEnables = newEnables
        this.activeOverrides = newOverrides

        this.$nextTick(() => {
          this.form.setFieldsValue(dynamicValues)
        })
      }
    })
  },
  methods: {
    onEnableChange (platform, checked) {
      this.activeEnables = {
        ...this.activeEnables,
        [platform]: checked
      }
    },
    onOverrideChange (platform, checked) {
      this.activeOverrides = {
        ...this.activeOverrides,
        [platform]: checked
      }
    }
  }
}
</script>

<style scoped>
::v-deep .ant-tabs-nav .ant-tabs-tab {
  font-size: 13px !important;
  margin-right: 16px !important;
  padding: 6px 10px !important;
}
</style>
