import { api } from "./axios";

export type Stock = {
  id: number;
  ticker: string;
  companyName: string | null;
  cik: string | null;
};

export async function getStocks() {
  const response = await api.get<Stock[]>("/stocks");
  return response.data;
}