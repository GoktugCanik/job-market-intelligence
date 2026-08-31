import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { useApiData } from '../hooks/useApiData';
import { ChartCard } from './ChartCard';
import type { TechnologyCombination } from '../api/types';

export function TechnologyCombinationsChart() {
  const { data, loading, error } = useApiData<TechnologyCombination[]>(
    '/api/analytics/technology-combinations?limit=10'
  );

  const chartData = (data ?? []).map((combo) => ({
    pair: `${combo.technologyA} + ${combo.technologyB}`,
    count: combo.count,
  }));

  return (
    <ChartCard
      title="Most Common Technology Combinations"
      loading={loading}
      error={error}
      isEmpty={!chartData.length}
    >
      <ResponsiveContainer width="100%" height={320}>
        <BarChart data={chartData} layout="vertical" margin={{ left: 48, right: 16 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis type="number" allowDecimals={false} />
          <YAxis type="category" dataKey="pair" width={180} />
          <Tooltip />
          <Bar dataKey="count" name="Jobs" fill="#7c3aed" />
        </BarChart>
      </ResponsiveContainer>
    </ChartCard>
  );
}
