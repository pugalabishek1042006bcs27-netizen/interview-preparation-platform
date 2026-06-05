import { useState, useEffect } from "react";
import Navbar from "../components/Navbar";
import API from "../services/api";
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as ChartTooltip,
  ResponsiveContainer,
  Radar,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
} from "recharts";

const DEFAULT_RADAR = [
  { subject: "DSA", A: 0, fullMark: 100 },
  { subject: "Java Core", A: 0, fullMark: 100 },
  { subject: "System Design", A: 0, fullMark: 100 },
  { subject: "Databases", A: 0, fullMark: 100 },
  { subject: "HR / Soft Skills", A: 0, fullMark: 100 },
  { subject: "DevOps", A: 0, fullMark: 100 },
];

const ProgressTracker = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    API.get("/progress/stats")
      .then((res) => setStats(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const performanceHistory = stats?.performanceHistory ?? [];
  const recentActivity = stats?.recentActivity ?? [];
  const skillSubjects = stats?.skillAnalysis?.subjects;
  const radarData =
    skillSubjects && skillSubjects.length > 0 ? skillSubjects : DEFAULT_RADAR;

  const totalMockInterviews = stats?.totalMockInterviews ?? 0;
  const averageScore = stats?.averageScore
    ? `${Math.round(stats.averageScore)}%`
    : "—";
  const questionsSolved = stats?.questionsSolved ?? 0;
  const currentStreak = stats?.currentStreak ?? 0;

  if (loading) {
    return (
      <div className="app-shell">
        <Navbar />
        <main
          className="page-content"
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            height: "60vh",
          }}
        >
          <p style={{ color: "var(--text-muted)" }}>Loading analytics...</p>
        </main>
      </div>
    );
  }

  return (
    <div className="app-shell">
      <Navbar />

      <main className="page-content">
        <header className="page-header">
          <div className="page-heading">
            <span className="section-badge blue">Performance Analytics</span>
            <h1 className="page-title">Analytics Overview</h1>
            <p className="page-subtitle">
              Track your interview performance, identify weak areas, and monitor
              your overall readiness for technical assessments over time.
            </p>
          </div>
        </header>

        <div className="stats-grid" style={{ marginBottom: "30px" }}>
          <div className="stat-card">
            <p className="stat-label">Total Mock Interviews</p>
            <p className="stat-value">{totalMockInterviews}</p>
          </div>
          <div className="stat-card">
            <p className="stat-label">Average Score</p>
            <p className="stat-value">{averageScore}</p>
          </div>
          <div className="stat-card">
            <p className="stat-label">Questions Solved</p>
            <p className="stat-value">{questionsSolved}</p>
          </div>
          <div className="stat-card">
            <p className="stat-label">Current Streak</p>
            <p className="stat-value">
              {currentStreak} {currentStreak === 1 ? "Day" : "Days"}
            </p>
          </div>
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(450px, 1fr))",
            gap: "24px",
            marginBottom: "40px",
          }}
        >
          <section className="panel">
            <h3 className="section-title" style={{ marginBottom: "20px" }}>
              Execution Readiness
            </h3>
            {performanceHistory.length === 0 ? (
              <div
                style={{
                  height: "300px",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                <p style={{ color: "var(--text-muted)" }}>
                  No performance data yet. Complete some mock interviews to see
                  your progress.
                </p>
              </div>
            ) : (
              <div style={{ height: "300px", width: "100%" }}>
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={performanceHistory}>
                    <CartesianGrid
                      strokeDasharray="3 3"
                      vertical={false}
                      stroke="var(--border)"
                    />
                    <XAxis
                      dataKey="name"
                      axisLine={false}
                      tickLine={false}
                      tick={{ fill: "var(--text-muted)", fontSize: 12 }}
                    />
                    <YAxis
                      axisLine={false}
                      tickLine={false}
                      tick={{ fill: "var(--text-muted)", fontSize: 12 }}
                      domain={[0, 100]}
                    />
                    <ChartTooltip />
                    <Area
                      type="monotone"
                      dataKey="avgScore"
                      stroke="var(--blue)"
                      fill="var(--blue-soft)"
                      strokeWidth={2}
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            )}
          </section>

          <section className="panel">
            <h3 className="section-title" style={{ marginBottom: "20px" }}>
              Skill Domain Analysis
            </h3>
            <div style={{ height: "300px", width: "100%" }}>
              <ResponsiveContainer width="100%" height="100%">
                <RadarChart outerRadius="70%" data={radarData}>
                  <PolarGrid stroke="var(--border)" />
                  <PolarAngleAxis
                    dataKey="subject"
                    tick={{
                      fill: "var(--text-soft)",
                      fontSize: 11,
                      fontWeight: 600,
                    }}
                  />
                  <Radar
                    name="Proficiency"
                    dataKey="A"
                    stroke="var(--purple)"
                    fill="var(--purple-soft)"
                    fillOpacity={0.6}
                  />
                </RadarChart>
              </ResponsiveContainer>
            </div>
          </section>
        </div>

        <section className="panel">
          <h3 className="section-title" style={{ marginBottom: "20px" }}>
            Recent Tactical Activity
          </h3>
          {recentActivity.length === 0 ? (
            <p style={{ color: "var(--text-muted)", padding: "16px 0" }}>
              No activity recorded yet. Start with a mock interview or the daily
              challenge.
            </p>
          ) : (
            <div className="questions-list">
              {recentActivity.map((act, i) => (
                <div
                  key={i}
                  className="question-card"
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    padding: "16px 24px",
                  }}
                >
                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: "16px",
                    }}
                  >
                    <div
                      className={`section-badge ${act.color}`}
                      style={{ width: "12px", height: "12px", padding: 0 }}
                    />
                    <div>
                      <p style={{ fontWeight: 700, color: "var(--text-main)" }}>
                        {act.action}
                      </p>
                      <p className="helper-text">{act.date}</p>
                    </div>
                  </div>
                  <div
                    style={{
                      fontWeight: 800,
                      fontSize: "1.1rem",
                      color: `var(--${act.color})`,
                    }}
                  >
                    {act.score}
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

export default ProgressTracker;
