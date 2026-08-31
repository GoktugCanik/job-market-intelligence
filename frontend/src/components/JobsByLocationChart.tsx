import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { useApiData } from '../hooks/useApiData';
import { ChartCard } from './ChartCard';
import type { LocationCount } from '../api/types';

export function JobsByLocationChart() {
  const { data, loading, error } = useApiData<LocationCount[]>('/api/analytics/jobs-by-location');

  return (
    <ChartCard title="Jobs by Location" loading={loading} error={error} isEmpty={!data?.length}>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data ?? []} margin={{ bottom: 8 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="location" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Bar dataKey="count" name="Jobs" fill="#059669" />
        </BarChart>
      </ResponsiveContainer>
    </ChartCard>
  );
}
