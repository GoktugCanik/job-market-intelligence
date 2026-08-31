import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { useApiData } from '../hooks/useApiData';
import { ChartCard } from './ChartCard';
import type { ExperienceLevelCount } from '../api/types';

export function JobsByExperienceLevelChart() {
  const { data, loading, error } = useApiData<ExperienceLevelCount[]>(
    '/api/analytics/jobs-by-experience-level'
  );

  return (
    <ChartCard title="Jobs by Experience Level" loading={loading} error={error} isEmpty={!data?.length}>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data ?? []} margin={{ bottom: 8 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="experienceLevel" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Bar dataKey="count" name="Jobs" fill="#d97706" />
        </BarChart>
      </ResponsiveContainer>
    </ChartCard>
  );
}
