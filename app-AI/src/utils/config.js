/**
 * 全局配置：后端接口地址
 *
 * H5 端：同源部署在 http://localhost:8080/m/index.html，直接用相对路径即可
 * 小程序/App 端：无同源概念，需要写后端的完整地址。
 *   开发时可填 http://localhost:8080（微信开发者工具需勾选「不校验合法域名」）；
 *   真机联调时请改为电脑的局域网 IP，例如 http://192.168.1.100:8080
 */
let BASE_URL = ''
// #ifndef H5
// App/小程序运行在手机上时访问不到 localhost，必须填电脑的局域网 IP。
// 查看方式：电脑 cmd 运行 ipconfig，找「IPv4 地址」。
// 注意：IP 变了（如换 WiFi）需要改这里并重新打包；手机需与电脑在同一网络。
BASE_URL = 'http://192.168.0.103:8080'
// #endif

export default { BASE_URL }
