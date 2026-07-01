// Labeled select dropdown.
// options: array of { value, label }
import { useId } from "react";

export default function SelectField({
  label,
  value,
  onChange,
  options = [],
  placeholder,
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
      <select
        id={id}
        className="field-select"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        {...rest}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      {error && <span className="field-error">{error}</span>}
    </div>
  );
}
