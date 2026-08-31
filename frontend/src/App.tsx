import { JobsByEmploymentTypeChart } from './components/JobsByEmploymentTypeChart';
import { JobsByExperienceLevelChart } from './components/JobsByExperienceLevelChart';
import { JobsByLocationChart } from './components/JobsByLocationChart';
import { JobsOverTimeChart } from './components/JobsOverTimeChart';
import { TechnologyCombinationsChart } from './components/TechnologyCombinationsChart';
import { TopTechnologiesChart } from './components/TopTechnologiesChart';

function App() {
  return (
    <>
      <header className="dashboard-header">
        <h1>Job Market Intelligence</h1>
        <p>Live analytics from the JMI backend API.</p>
      </header>
      <main className="dashboard-grid">
        <TopTechnologiesChart />
        <JobsByLocationChart />
        <JobsByExperienceLevelChart />
        <JobsByEmploymentTypeChart />
        <JobsOverTimeChart />
        <TechnologyCombinationsChart />
      </main>
    </>
  );
}

export default App;
