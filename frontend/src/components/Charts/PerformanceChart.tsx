import React from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  ReferenceLine,
} from 'recharts';

interface PerformanceData {
  date: string;
  value: number;
  changePercent: number;
}

interface PerformanceChartProps {
  data: PerformanceData[];
  title?: string;
}

export const PerformanceChart: React.FC<PerformanceChartProps> = ({ data, title = 'Performance' }) => {
  const minValue = Math.min(...data.map((d) => d.value));
  const maxValue = Math.max(...data.map((d) => d.value));

  return (
    <div className="performance-chart-container">
      <h3>{title}</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
          <XAxis dataKey="date" stroke="#666" />
          <YAxis stroke="#666" domain={[minValue * 0.99, maxValue * 1.01]} />
          <Tooltip
            formatter={(value) => `$${Number(value).toFixed(2)}`}
            contentStyle={{
              backgroundColor: '#ffffff',
              border: '1px solid #ccc',
              borderRadius: '4px',
              padding: '8px',
            }}
            labelStyle={{ color: '#000' }}
          />
          <ReferenceLine
            y={data[0]?.value}
            stroke="#999"
            strokeDasharray="3 3"
            label={{ value: 'Start', position: 'right', fill: '#666', fontSize: 12 }}
          />
          <Line
            type="monotone"
            dataKey="value"
            stroke="#8b5cf6"
            strokeWidth={2}
            dot={false}
            name="Portfolio Value"
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};
