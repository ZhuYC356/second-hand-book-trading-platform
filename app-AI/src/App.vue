<script>
export default {
  onLaunch: function () {},
  onShow: function () {},
  onHide: function () {},
}
</script>

<style lang="scss">
page {
  background: #f6f7fb;
  color: #1f2937;
  font-size: 28rpx;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue',
    'Microsoft YaHei', sans-serif;
}

/* 去除按钮默认样式 */
button {
  padding: 0;
  margin: 0;
  background: transparent;
  border-radius: 0;
  line-height: inherit;
  font-size: inherit;
}
button::after {
  border: none;
}

/* H5 端隐藏原生 tabBar（兜底，配合 main.js 全局 hideTabBar） */
/* #ifdef H5 */
uni-tabbar {
  display: none !important;
}
/* #endif */

/* 通用省略 */
.ellipsis-1 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.ellipsis-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
