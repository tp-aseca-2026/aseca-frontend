import { api } from "./axios";

export type TransactionType = "BUY" | "SELL";

export type Transaction = {
  id: number;
  userId: number;
  stockId: number;
  type: TransactionType;
  quantity: number;
  price: number;
  executedAt: string;
};

export async function buyTransaction(ticker: string, quantity: number) {
  const response = await api.post<Transaction>("/transactions/buy", {
    ticker,
    quantity,
  });

  return response.data;
}

export async function sellTransaction(ticker: string, quantity: number) {
  const response = await api.post<Transaction>("/transactions/sell", {
    ticker,
    quantity,
  });

  return response.data;
}