const { override } = require('customize-cra');

module.exports = override(
  // 自定义webpack配置
  (config) => {
    return config;
  }
); 