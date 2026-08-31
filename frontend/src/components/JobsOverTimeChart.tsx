import { useState } from 'react';
import { CartesianGrid, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { useApiData } from '../hooks/useApiData';
import { ChartCard } from './ChartCard';
import type { JobsOverTime } from '../api/types';

type Granularity = 'week' | 'month';

export function JobsOverTimeChart() {
  const [granularity, setGranularity] = useState<Granularity>('month');
  const { data, loading, error } = useApiData<JobsOverTime[]>(
    `/api/analytics/jobs-over-time?granularity=${granularity}`
  );

  return (
    <ChartCard title="Jobs Over Time" loading={loading} error={error} isEmpty={!data?.length}>
      <div className="chart-controls">
        <label>
          <input
            type="radio"
            name="granularity"
            checked={granularity === 'week'}
            onChange={() => setGranularity('week')}
          />
          Weekly
        </label>
        <label>
          <input
            type="radio"
            name="granularity"
            checked={granularity === 'month'}
            onChange={() => setGranularity('month')}
          />
          Monthly
        </label>
      </div>
      <ResponsiveContainer width="100%" height={280}>
        <LineChart data={data ?? []} margin={{ bottom: 8 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="period" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Line type="monotone" dataKey="count" name="Jobs posted" stroke="#2563eb" strokeWidth={2} />
        </LineChart>
      </ResponsiveContainer>
    </ChartCard>
  );
}
