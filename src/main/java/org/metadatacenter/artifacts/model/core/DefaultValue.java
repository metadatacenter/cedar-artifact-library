
package org.metadatacenter.artifacts.model.core;

public sealed interface DefaultValue<T> permits StringDefaultValue, NumericDefaultValue, URIStringPairDefaultValue
{
   DefaultValueType getValueType();

   T getValue();

   default boolean isStringDefaultValue() { return getValueType() == DefaultValueType.STRING; }

   default boolean isNumericDefaultValue() { return getValueType() == DefaultValueType.NUMERIC; }

   default boolean isURIStringPairDefaultValue() { return getValueType() == DefaultValueType.URI_STRING_PAIR; }

   default StringDefaultValue asStringDefaultValue()
   {
      if (getValueType() == DefaultValueType.STRING)
         return (StringDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + StringDefaultValue.class.getName());
   }

   default NumericDefaultValue asNumericDefaultValue()
   {
      if (getValueType() == DefaultValueType.NUMERIC)
         return (NumericDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + NumericDefaultValue.class.getName());
   }

   default URIStringPairDefaultValue asURIStringPairDefaultValue()
   {
      if (getValueType() == DefaultValueType.URI_STRING_PAIR)
         return (URIStringPairDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + URIStringPairDefaultValue.class.getName());
   }
}
