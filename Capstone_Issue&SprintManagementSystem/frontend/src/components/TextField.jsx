// Labeled text input. Associates the label with the input for accessibility.
import { useId } from "react";

export default function TextField({
  label,
  value,
  onChange,
  type = "text",
  error,
  ...rest
}) {
  const id = useId();
  return (
    <div className="field">
      {label && (
        <label className="field-label" htmlFor={id}>
          {label}
        </label>
      )}
      <input
        id={id}
        className="field-input"
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        {...rest}
      />
      {error && <span className="field-error">{error}</span>}
    </div>
  );
}
