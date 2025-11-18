import React from 'react';
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  ReferenceLine,
} from 'recharts';
import { HistoricalData } from '../../services/marketService';

interface PriceChartProps {
  data: HistoricalData[];
  showArea?: boolean;
  showMA?: boolean; // Moving Average
}

export const PriceChart: React.FC<PriceChartProps> = ({ data, showArea = false, showMA = false }) => {
  // Calculate 20-day moving average
  const dataWithMA = showMA
    ? data.map((item, index) => {
        if (index < 19) return item;
        const sum = data.slice(index - 19, index + 1).reduce((acc, d) => acc + d.close, 0);
        return {
          ...item,
          ma20: sum / 20,
        };
      })
    : data;

  if (showArea) {
    return (
      <ResponsiveContainer width="100%" height={400}>
        <AreaChart data={dataWithMA}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
          <XAxis dataKey="date" stroke="#666" />
          <YAxis stroke="#666" />
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
          <Legend />
          <Area
            type="monotone"
            dataKey="close"
            stroke="#2563eb"
            name="Close Price"
            fill="#2563eb"
            fillOpacity={0.1}
            dot={false}
            strokeWidth={2}
          />
          {showMA && data.length > 20 && (
            <Line
              type="monotone"
              dataKey="ma20"
              stroke="#f59e0b"
              name="20-Day MA"
              dot={false}
              strokeWidth={2}
              strokeDasharray="5 5"
            />
          )}
        </AreaChart>
      </ResponsiveContainer>
    );
  }

  return (
    <ResponsiveContainer width="100%" height={400}>
      <LineChart data={dataWithMA}>
        <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
        <XAxis dataKey="date" stroke="#666" />
        <YAxis stroke="#666" />
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
        <Legend />
        <Line
          type="monotone"
          dataKey="close"
          stroke="#2563eb"
          name="Close Price"
          dot={false}
          strokeWidth={2}
        />
        {showMA && data.length > 20 && (
          <Line
            type="monotone"
            dataKey="ma20"
            stroke="#f59e0b"
            name="20-Day MA"
            dot={false}
            strokeWidth={2}
            strokeDasharray="5 5"
          />
        )}
      </LineChart>
    </ResponsiveContainer>
  );
};
