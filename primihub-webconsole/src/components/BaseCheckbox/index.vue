<template>
  <div>
    <el-checkbox v-model="checkAll" :indeterminate="isIndeterminate" @change="handleCheckAllChange">全选</el-checkbox>
    <template v-if="options.length > 0">
      <el-checkbox-group v-model="checkedList" @change="handleCheckedChange">
        <el-checkbox v-for="item in options" :key="item" :label="item">{{ item }}</el-checkbox>
      </el-checkbox-group>
    </template>
    <p v-else class="no-data">暂无数据</p>

  </div>
</template>

<script>
export default {
  props: {
    organId: {
      type: String,
      default: ''
    },
    options: {
      type: Array,
      default: () => []
    },
    checked: {
      type: Array,
      default: () => []
    },
    selectData: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      checkedList: this.checked || [],
      currentOptions: this.options,
      isIndeterminate: true
    }
  },
  computed: {
    checkAll: {
      get() {
        return this.checked && this.options && this.checked.length === this.options.length
      },
      set() {

      }
    }
  },
  methods: {
    filterStatus(item) {
      const currentData = this.selectData.find(data => data === item)
      return !!currentData
    },
    handleCheckedChange(value) {
      this.isIndeterminate = value.length > 0 && value.length < this.options.length
      this.$emit('change', {
        organId: this.organId,
        checked: this.checkedList
      })
    },
    handleCheckAllChange(value) {
      const options = [...this.options]
      if (value) {
        for (let i = 0; i < options.length; i++) {
          const item = options[i]
          if (!this.selectData.includes(item)) {
            this.checkedList.push(item)
          }
        }
      } else {
        this.checkedList = []
      }
      this.isIndeterminate = true
      this.$emit('change', {
        organId: this.organId,
        checked: this.checkedList
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  .no-data{
    color: #999;
    margin: 10px 0;
    text-align: center;
  }
</style>
