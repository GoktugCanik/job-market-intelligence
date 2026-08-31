import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { useApiData } from '../hooks/useApiData';
import { ChartCard } from './ChartCard';
import type { EmploymentTypeCount } from '../api/types';

export function JobsByEmploymentTypeChart() {
  const { data, loading, error } = useApiData<EmploymentTypeCount[]>(
    '/api/analytics/jobs-by-employment-type'
  );

  return (
    <ChartCard title="Jobs by Employment Type" loading={loading} error={error} isEmpty={!data?.length}>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data ?? []} margin={{ bottom: 8 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="employmentType" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Bar dataKey="count" name="Jobs" fill="#db2777" />
        </BarChart>
      </ResponsiveContainer>
    </ChartCard>
  );
}
