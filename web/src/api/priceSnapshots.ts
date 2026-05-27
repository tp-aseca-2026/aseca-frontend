import { api } from "./axios";

export type PriceSnapshot = {
  ticker: string;
  stockId: number;
  price: string | number;
  source: string;
  fetchedAt: string;
};

export type LatestPriceSnapshotsResponse = {
  lastUpdatedAt: string | null;
  prices: PriceSnapshot[];
};

export type PriceSnapshotUpdateResult = {
  processed: number;
  saved: number;
  failed: { ticker: string; error: string }[];
  updatedAt: string | null;
};

export async function getLatestPriceSnapshots(): Promise<LatestPriceSnapshotsResponse> {
  const response = await api.get<LatestPriceSnapshotsResponse>(
    "/price-snapshots/latest",
  );

  return response.data;
}

export async function updatePriceSnapshots(
  tickers: string[],
): Promise<PriceSnapshotUpdateResult> {
  const response = await api.post<PriceSnapshotUpdateResult>(
    "/price-snapshots/update",
    { tickers },
  );

  return response.data;
}