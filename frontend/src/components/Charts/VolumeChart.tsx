import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import { HistoricalData } from '../../services/marketService';

interface VolumeChartProps {
  data: HistoricalData[];
}

export const VolumeChart: React.FC<VolumeChartProps> = ({ data }) => {
  return (
    <ResponsiveContainer width="100%" height={200}>
      <BarChart data={data}>
        <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
        <XAxis dataKey="date" stroke="#666" />
        <YAxis stroke="#666" />
        <Tooltip
          formatter={(value) => `${(Number(value) / 1000000).toFixed(2)}M`}
          contentStyle={{
            backgroundColor: '#ffffff',
            border: '1px solid #ccc',
            borderRadius: '4px',
            padding: '8px',
          }}
          labelStyle={{ color: '#000' }}
        />
        <Bar
          dataKey="volume"
          fill="#10b981"
          name="Trading Volume"
          radius={[4, 4, 0, 0]}
        />
      </BarChart>
    </ResponsiveContainer>
  );
};
