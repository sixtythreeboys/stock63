import config from 'config';

export type Markets =
  | 'NYS'
  | 'NAS'
  | 'AMS'
  | 'TSE'
  | 'HKS'
  | 'SHS'
  | 'SZS'
  | 'HSX'
  | 'HNX';

export type HHDFS76410000 = {
  AUTH: '';
  EXCD: Markets;
  CO_YN_PRICECUR?: string;
  CO_ST_PRICECUR?: string;
  CO_EN_PRICECUR?: string;
  CO_YN_RATE?: string;
  CO_ST_RATE?: string;
  CO_EN_RATE?: string;
  CO_YN_VALX?: string;
  CO_ST_VALX?: string;
  CO_EN_VALX?: string;
  CO_YN_SHAR?: string;
  CO_ST_SHAR?: string;
  CO_EN_SHAR?: string;
  CO_YN_VOLUME?: string;
  CO_ST_VOLUME?: string;
  CO_EN_VOLUME?: string;
  CO_YN_AMT?: string;
  CO_ST_AMT?: string;
  CO_EN_AMT?: string;
  CO_YN_EPS?: string;
  CO_ST_EPS?: string;
  CO_EN_EPS?: string;
  CO_YN_PER?: string;
  CO_ST_PER?: string;
  CO_EN_PER?: string;
};

export type Header = {
  'content-type'?: string;
  authorization?: string;
  appkey?: string;
  appsecret?: string;
  personalseckey?: string;
  tr_id: string;
  tr_cont?: string;
  custtype?: string;
  seq_no?: string;
  mac_address?: string;
  phone_number?: string;
  ip_addr?: string;
  hashkey?: string;
  gt_uid?: string;
};
export function makeHeader(params?: Header): Header {
  const res = {
    'content-type': 'application/json',
    appkey: config.KIS.appkey,
    appsecret: config.KIS.appsecret,
  };
  for (const key of Object.keys(params)) {
    res[key] = params[key];
  }
  return res as Header;
}
