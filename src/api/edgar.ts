import { api } from "./axios";

export type EdgarCompany = {
  companyName: string;
  ticker: string;
  cik: string;
};

export type EdgarMetricPoint = {
  fy: number;
  fp: string;
  end: string;
  filed: string;
  form: string;
  val: number;
};

export type EdgarMetrics = {
  revenue: EdgarMetricPoint | null;
  netIncome: EdgarMetricPoint | null;
  eps: EdgarMetricPoint | null;
  totalAssets: EdgarMetricPoint | null;
  totalLiabilities: EdgarMetricPoint | null;
};

export type EdgarHistoricalMetrics = {
  revenue: EdgarMetricPoint[];
  netIncome: EdgarMetricPoint[];
  eps: EdgarMetricPoint[];
  totalAssets: EdgarMetricPoint[];
  totalLiabilities: EdgarMetricPoint[];
};

export type EdgarFiling = {
  form: string;
  filingDate: string;
  accessionNumber: string;
  reportDate: string | null;
  primaryDocument: string | null;
  link: string;
};

export const edgarApi = {
  async searchCompanies(query: string): Promise<EdgarCompany[]> {
    const response = await api.get<EdgarCompany[]>("/edgar/companies/search", {
      params: { q: query },
    });

    return response.data;
  },

  async getCompanyMetrics(ticker: string): Promise<EdgarMetrics> {
    const response = await api.get<EdgarMetrics>(
      `/edgar/companies/${ticker}/metrics`
    );

    return response.data;
  },

  async getCompanyFilings(ticker: string): Promise<EdgarFiling[]> {
    const response = await api.get<EdgarFiling[]>(
      `/edgar/companies/${ticker}/filings`
    );

    return response.data;
  },

  async getHistoricalMetrics(
    ticker: string
  ): Promise<EdgarHistoricalMetrics> {
    const response = await api.get<EdgarHistoricalMetrics>(
      `/edgar/companies/${ticker}/historical-metrics`
    );

    return response.data;
  },
};