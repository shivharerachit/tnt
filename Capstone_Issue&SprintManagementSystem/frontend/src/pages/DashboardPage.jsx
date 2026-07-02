// Dashboard: list of projects + create project.
import { useState } from "react";
import { Link } from "../lib/router";
import { getProjects, createProject } from "../services/projects";
import { getUsers } from "../services/users";
import { useAuth } from "../hooks/useAuth";
import { useAsyncData } from "../hooks/useAsyncData";
import { isRequired } from "../utils/validation";
import { USER_ROLE } from "../constants";
import AppLayout from "../components/AppLayout";
import Button from "../components/Button";
import TextField from "../components/TextField";
import { LoadingState, ErrorState, EmptyState } from "../components/DataStates";

export default function DashboardPage() {
    const { user } = useAuth();
    const {
        data: projects,
        isLoading,
        error,
        reload,
    } = useAsyncData(getProjects);
    const { data: users } = useAsyncData(getUsers);

    const isAdmin = user?.role === USER_ROLE.ADMIN;

    return (
        <AppLayout>
            <div className="page-header">
                <div>
                    <h1 className="page-title">Projects</h1>
                    <p className="page-subtitle">All projects you have access to.</p>
                </div>
                {isAdmin && (
                    <Button
                    //  onClick={}
                     >+ New Project</Button>
                )}
            </div>
            {isLoading && <LoadingState message="Loading projects..." />}
            {!isLoading && error && <ErrorState message={error} onRetry={reload} />}

            {!isLoading && !error && projects && projects.length === 0 && (
                <EmptyState
                    title="No projects yet"
                    description={
                        isAdmin
                            ? "Create your first project to get started."
                            : "You have not been added to any projects yet."
                    }
                    action={
                        isAdmin && (
                            <Button 
                            // onClick={}
                            >
                                Create a project
                            </Button>
                        )
                    }
                />
            )}

            {!isLoading && !error && projects && projects.length > 0 && (
                <div className="card-grid">
                    {projects.map((project) => (
                        <Link
                            key={project.id}
                            params={{ projectId: project.id }}
                            className="card clickable-card"
                            style={{ color: "inherit" }}
                        >
                            <div className="row">
                                <span className="badge badge-primary">{project.key}</span>
                                <span className="text-muted text-sm">
                                    {project.memberIds?.length || 0} members
                                </span>
                            </div>
                            <h3 style={{ marginTop: "12px" }}>{project.name}</h3>
                            <p className="text-muted text-sm" style={{ marginTop: "6px" }}>
                                {project.description || "No description provided."}
                            </p>
                        </Link>
                    ))}
                </div>
            )}
        </AppLayout>
    );
}
