const { override, overrideDevServer } = require('customize-cra');

// 自定义开发服务器配置
const devServerConfig = () => config => {
  return {
    ...config,
    allowedHosts: 'all',  // 允许所有主机访问
    host: '127.0.0.1',
    port: 3000,
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    client: {
      webSocketURL: 'ws://127.0.0.1:3000/ws',
    },
  };
};

module.exports = {
  webpack: override(
    // 你可以在这里添加webpack配置
  ),
  devServer: overrideDevServer(devServerConfig())
}; 