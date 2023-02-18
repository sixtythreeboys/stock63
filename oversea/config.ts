export default {
  APP: {
    PORT: 8082,
  },
  KIS_URL: {
    real: {},
    mock: {},
    urls: {
      'v1_해외주식-009': '/uapi/overseas-price/v1/quotations/price',
    },
  },
  EUREKA: {
    instance: {
      app: 'my-node-app',
      hostName: 'localhost',
      ipAddr: '127.0.0.1',
      port: {
        $: 3000,
        '@enabled': 'true',
      },
      vipAddress: 'my-node-app',
      dataCenterInfo: {
        '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
        name: 'MyOwn',
      },
    },
    eureka: {
      host: 'eureka-server-hostname',
      port: 8761,
      servicePath: '/eureka/apps/',
    },
  },
};
