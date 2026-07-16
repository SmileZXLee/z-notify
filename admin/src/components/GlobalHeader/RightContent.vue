<template>
  <div :class="wrpCls" style="align-items: ;center">
    <a-tag v-if="appVersion" color="orange">
      {{ appVersion }}
    </a-tag>
    <avatar-dropdown :menu="showMenu" :current-user="currentUser" :class="prefixCls" />
    <!-- <select-lang :class="prefixCls" /> -->
    <a target="_blank" href="https://github.com/SmileZXLee/z-notify-admin-vue" style="color: #000000">
      <a-icon style="font-size: 20px;margin: 0px 10px" type="github"></a-icon>
    </a>
  </div>
</template>

<script>
import AvatarDropdown from './AvatarDropdown'
import SelectLang from '@/components/SelectLang'

export default {
  name: 'RightContent',
  components: {
    AvatarDropdown,
    SelectLang
  },
  props: {
    prefixCls: {
      type: String,
      default: 'ant-pro-global-header-index-action'
    },
    isMobile: {
      type: Boolean,
      default: () => false
    },
    topMenu: {
      type: Boolean,
      required: true
    },
    theme: {
      type: String,
      required: true
    }
  },
  data () {
    return {
      showMenu: true,
      currentUser: {}
    }
  },
  computed: {
    wrpCls () {
      return {
        'ant-pro-global-header-index-right': true,
        [`ant-pro-global-header-index-${(this.isMobile || !this.topMenu) ? 'light' : this.theme}`]: true
      }
    },
    appVersion () {
      return process.env.VUE_APP_VERSION
    }
  },
  mounted () {
    setTimeout(() => {
      this.currentUser = {
        name: localStorage.getItem('account') || '佚名'
      }
    }, 10)
  }
}
</script>
