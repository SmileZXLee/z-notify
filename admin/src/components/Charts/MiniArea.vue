<template>
  <div class="antv-chart-mini">
    <div class="chart-wrapper" :style="{ height: 46 }">
      <v-chart :force-fit="true" :height="height" :data="chartData" :padding="[36, 0, 18, 0]">
        <v-tooltip />
        <v-smooth-area position="x*count" />
      </v-chart>
    </div>
  </div>
</template>

<script>
import moment from 'moment'

export default {
  name: 'MiniArea',
  props: {
    type: {
      type: String,
      default: 'date'
    },
    list: {
      type: Array,
      default: function () {
        return []
      }
    }
  },
  computed: {
    chartData () {
      return this.orginData.map(data => {
        data.count = (this.list.find(i => (i[this.type] + (this.type === 'hour' ? '点' : '')) === data.x + '') || { count: 0 }).count
        return data
      })
    }
  },
  data () {
    return {
      orginData: [],
      height: 100
    }
  },
  mounted () {
    const orginData = []
    if (this.type === 'date') {
      const beginDay = new Date().getTime()
      for (let i = 0; i < 7; i++) {
        orginData.push({
          x: moment(new Date(beginDay - 1000 * 60 * 60 * 24 * i)).format('YYYY-MM-DD'),
          count: 0
        })
      }
    } else if (this.type === 'hour') {
      for (let i = 0; i < 24; i++) {
        orginData.push({
          x: i + '点',
          count: 0
        })
      }
    }
    this.orginData = orginData
  }
}
</script>

<style lang="less" scoped>
  @import "chart";
</style>
