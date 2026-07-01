// Small router adapter built on top of react-router-dom.

import {
  Link as RouterLink,
  NavLink,
  useNavigate as useRouterNavigate,
  useParams as useRouterParams,
} from "react-router-dom";

function buildPath(to, params = {}) {
  if (!to) return "/";
  return to
    .split("/")
    .map((segment) => {
      if (segment.startsWith("$")) {
        const key = segment.slice(1);
        return params[key] ?? "";
      }
      return segment;
    })
    .join("/");
}

export function Link({ to, params, activeProps, className, ...rest }) {
  const href = buildPath(to, params);

  if (activeProps) {
    return (
      <NavLink
        to={href}
        className={({ isActive }) =>
          isActive ? activeProps.className || className : className
        }
        {...rest}
      />
    );
  }

  return <RouterLink to={href} className={className} {...rest} />;
}

export function useNavigate() {
  const navigate = useRouterNavigate();
  return (options) => {
    if (typeof options === "string") {
      navigate(options);
      return;
    }
    navigate(buildPath(options.to, options.params));
  };
}

export function useParams() {
  return useRouterParams();
}
