import type { ReactNode } from 'react';

interface ChartCardProps {
  title: string;
  loading: boolean;
  error: string | null;
  isEmpty: boolean;
  children: ReactNode;
}

export function ChartCard({ title, loading, error, isEmpty, children }: ChartCardProps) {
  return (
    <div className="chart-card">
      <h2>{title}</h2>
      {loading && <p className="status">Loading…</p>}
      {error && <p className="status status-error">Failed to load: {error}</p>}
      {!loading && !error && isEmpty && <p className="status">No data yet.</p>}
      {!loading && !error && !isEmpty && children}
    </div>
  );
}
