import { api } from "./axios";

export type WatchlistItem = {
  id: number;
  userId: number;
  stockId: number;
  createdAt?: string;
  stock: {
    id: number;
    ticker: string;
    companyName: string | null;
    cik?: string | null;
  };
};

export type WatchlistComparisonItem = {
  ticker: string;
  companyName: string | null;
  revenue: { val: number } | null;
  netIncome: { val: number } | null;
  eps: { val: number } | null;
  totalAssets: { val: number } | null;
  totalLiabilities: { val: number } | null;
};

export async function getWatchlist(): Promise<WatchlistItem[]> {
  const response = await api.get<WatchlistItem[]>("/watchlist");
  return response.data;
}

export async function getWatchlistComparison(): Promise<
  WatchlistComparisonItem[]
> {
  const response = await api.get<WatchlistComparisonItem[]>(
    "/watchlist/comparison",
  );

  return response.data;
}

export async function addToWatchlist(ticker: string): Promise<WatchlistItem> {
  const response = await api.post<WatchlistItem>("/watchlist", { ticker });
  return response.data;
}

export async function removeFromWatchlist(ticker: string): Promise<void> {
  await api.delete(`/watchlist/${ticker}`);
}