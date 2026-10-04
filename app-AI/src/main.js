import {
	createSSRApp
} from "vue";
import App from "./App.vue";
export function createApp() {
	const app = createSSRApp(App);
	// 底部导航使用自定义 app-tabbar 组件，隐藏各端原生 tabBar（无 tabBar 页面自动忽略）
	app.mixin({
		onShow() {
			uni.hideTabBar({ fail: () => {} })
		}
	})
	return {
		app,
	};
}
