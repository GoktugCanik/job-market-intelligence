import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { useApiData } from '../hooks/useApiData';
import { ChartCard } from './ChartCard';
import type { TechnologyCount } from '../api/types';

export function TopTechnologiesChart() {
  const { data, loading, error } = useApiData<TechnologyCount[]>('/api/analytics/technologies?limit=10');

  return (
    <ChartCard
      title="Most Requested Technologies"
      loading={loading}
      error={error}
      isEmpty={!data?.length}
    >
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data ?? []} layout="vertical" margin={{ left: 24, right: 16 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis type="number" allowDecimals={false} />
          <YAxis type="category" dataKey="technology" width={100} />
          <Tooltip />
          <Bar dataKey="count" name="Jobs requesting" fill="#4f46e5" />
        </BarChart>
      </ResponsiveContainer>
    </ChartCard>
  );
}
