<template>
  <div class="footer custom-render">
    <div class="ant-pro-global-footer">
      <div v-if="$slots.links || (links && links.length)" class="ant-pro-global-footer-links">
        <slot name="links">
          <template v-if="Array.isArray(links)">
            <a
              v-for="link in links"
              :key="link.key || link.title"
              :title="link.title"
              :target="link.blankTarget ? '_blank' : '_self'"
              :href="link.href"
            >{{ link.title }}</a>
          </template>
        </slot>
      </div>
      <div v-if="$slots.copyright || copyright" class="ant-pro-global-footer-copyright">
        <slot name="copyright">{{ copyright }}</slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProGlobalFooter',
  props: {
    links: {
      type: Array,
      default: () => []
    },
    copyright: {
      type: String,
      default: ''
    }
  }
}
</script>

<style lang="less" scoped>
.footer {
  padding: 0 16px;
  margin: 48px 0 24px 0;
  text-align: center;

  .ant-pro-global-footer-links {
    margin-bottom: 8px;

    a {
      color: rgba(0, 0, 0, 0.45);
      transition: all 0.3s;

      &:not(:last-child) {
        margin-right: 40px;
      }

      &:hover {
        color: rgba(0, 0, 0, 0.65);
      }
    }
  }

  .ant-pro-global-footer-copyright {
    color: rgba(0, 0, 0, 0.45);
    font-size: 14px;
  }
}
</style>
